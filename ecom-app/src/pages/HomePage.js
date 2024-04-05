import React, { useState, useEffect } from 'react';
import { PublicNavbar } from '../components/PublicNavbar';
import { Button, Card, Col, Container, Row, Toast, ToastContainer } from 'react-bootstrap';
import Product from '../media/product_placeholder.jpg'
import axios from 'axios';
import { CommonFooter } from '../components/CommonFooter';
import { useContext } from 'react';
import { AuthConsumer } from '../providers/authProvider';
import { BsCartPlus } from "react-icons/bs";
import { AdminNavbar } from '../components/AdminNavbar ';


function HomePage() {


    const auth = useContext(AuthConsumer)
    console.log(auth.isAuthenticated());
    console.log(auth.getAccessToken());

    const headers = {
        headers: { Authorization: `Bearer ${auth.getAccessToken()}` }
    };

    const [addItem, setAddItem] = useState({
        productId: ""
    });
    const [addItemTrigger, setAddItemTrigger] = React.useState(false);


    const [showToast, setShowToast] = useState(false);
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });

    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };

    React.useEffect(() => {
        if (addItemTrigger) {
            const dataBody = { id: addItem.productId }
            axios.post("http://localhost:8080/cart", dataBody, headers).then((response) => {
                console.log(response);
                createToast({ title: "Item Added", body: "Item has been added to your cart successfully", type: "light" });
                setAddItemTrigger(false);
            }).catch((error) => {
                console.log(error);
                if (error.response.status === 401) {
                    auth.signinRedirect();
                }
                createToast({ title: "Item Not Added", body: error.response.data.message, type: "danger" });
                setAddItemTrigger(false);
            });
        }
    }, [addItem]);


    const [productList, setProductList] = React.useState([]);

    React.useEffect(() => {
        axios.get("http://localhost:8080/product").then((response) => {
            console.log(response.data);
            setProductList(response.data);
        });
    }, []);
    return (
        <>
            {auth.isAuthenticated() && auth.parseJwt(auth.getAccessToken())['roles'][0] === 'ADMIN' ? (
                < AdminNavbar></AdminNavbar>
            ) : (
                <PublicNavbar></PublicNavbar >
            )
            }
            <ToastContainer position="top-end" className="pt-5 mt-4 px-3" style={{ zIndex: 100000 }}>
                <Toast bg={toastData.type} onClose={() => setShowToast(false)} show={showToast} delay={3000} autohide >
                    <Toast.Header>
                        <strong className="me-auto">{toastData.title}</strong>
                        <small>Now</small>
                    </Toast.Header>
                    <Toast.Body>{toastData.body}</Toast.Body>
                </Toast>
            </ToastContainer>
            <Container className='pt-4 h-100 w-75' fluid>
                <Row xs={1} md={2} lg={3} className='px-0'>
                    {productList.map((item, idx) => (
                        <Col key={idx} className='mt-3' >
                            <Card bg="muted" border="dark">

                                <Card.Img variant="top" src={item['imageUrl']} height={"400"} width={"100%"} />
                                <Card.Body>
                                    <Card.Title className='row  me-auto'>
                                        <div className=' col-lg-6 col-md-6 fw-bold fs-5'>
                                            {item['name']}
                                        </div>
                                        <div className='d-flex col-lg-6 col-md-6 px-0 justify-content-end'>
                                            {auth.isAuthenticated() && auth.parseJwt(auth.getAccessToken())['roles'][0] === 'USER' ? (
                                                <Button className='mr-0 fs-6 fw-bold px-1 py-1' variant="outline-dark" onClick={() => { console.log(item['id']); setAddItemTrigger(true); setAddItem({ productId: item['id'] }); }} > Add to Cart</Button>
                                            ) : (
                                                <></>
                                            )} </div>

                                    </Card.Title>
                                    <Card.Subtitle className="mb-2 fs-4  text-dark">$ {item['price']}</Card.Subtitle>
                                    <Card.Text className='fw-light' >
                                        {item['description']}
                                        {/* <a href="/" class="stretched-link"></a> */}

                                    </Card.Text>

                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>

            </Container>
            <CommonFooter />
            
        </>
    );
}

export default HomePage;

// <Row xs={1} md={2} lg={4}>
//                     {Array.from({ length: 7 }).map((_, idx) => (
//                         <Col key={idx} className='mt-3' >
//                             <Card >
//                                 <Card.Img variant="top" src={Product} height={"100%"} />
//                                 <Card.Body>
//                                     <Card.Title>Card title</Card.Title>
//                                     <Card.Text>
//                                         This is a longer card with supporting text below as a natural
//                                         lead-in to additional content. This content is a little bit
//                                         longer.
//                                     </Card.Text>
//                                 </Card.Body>
//                             </Card>
//                         </Col>
//                     ))}
//                 </Row>
