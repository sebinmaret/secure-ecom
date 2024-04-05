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
import { Navigate, useNavigate } from 'react-router-dom';



function FeedbackListPage() {


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


    const [feedbackList, setFeedbackList] = React.useState([]);

    React.useEffect(() => {
        axios.get("http://localhost:8080/feedback", headers).then((response) => {
            console.log(response.data);
            setFeedbackList(response.data);
        }).catch((error) => {
            console.log(error)
            if(error.response.status===401){
                auth.signinRedirect();
            }
        });
    }, []);
    return (
        <>
            {!auth.isAuthenticated() || auth.parseJwt(auth.getAccessToken())['roles'][0] === 'USER' ? <Navigate replace={true} to='/' /> : <></>}
            {auth.isAuthenticated() && auth.parseJwt(auth.getAccessToken())['roles'][0] === 'ADMIN' ? (
                < AdminNavbar></AdminNavbar>
            ) : (
                <PublicNavbar></PublicNavbar >
            )
            }

            <Container className='pt-4 h-100 w-75' fluid>
                <Row xs={1} md={2} lg={3}>
                    {feedbackList.map((item, idx) => (
                        <Col key={idx} className='mt-3' >
                            <Card bg="muted" border="dark">
                                <Card.Body>
                                    <Card.Title className='row  me-auto my-2'>
                                        <div className=' col-6  fw-light fs-6'>
                                            <div className='text-muted fw-light fs-6 border-bottom'>
                                                Name
                                            </div> {item['name']}
                                        </div>
                                        <div className=' col-6  fw-light fs-6'>
                                            <div className='text-muted fw-light fs-6 border-bottom'>
                                                Date
                                            </div> {item['date']}
                                        </div>

                                    </Card.Title>
                                    <Card.Subtitle className="mb-2 fs-6 fw-light text-dark my-2">
                                        <div className='text-muted fw-light fs-6 border-bottom'>
                                            Email
                                        </div>{item['email']}</Card.Subtitle>
                                    <Card.Text className='fs-6 fw-light my-2' >
                                        <div className='text-muted fw-light fs-6 border-bottom'>
                                            Description
                                        </div>
                                        {item['description']}


                                    </Card.Text>

                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>

            </Container>
            <CommonFooter />
            <ToastContainer position="top-end" className="pt-5 mt-4 px-3" style={{ zIndex: 100000 }}>
                <Toast bg={toastData.type} onClose={() => setShowToast(false)} show={showToast} delay={3000} autohide >
                    <Toast.Header>
                        <strong className="me-auto">{toastData.title}</strong>
                        <small>Now</small>
                    </Toast.Header>
                    <Toast.Body>{toastData.body}</Toast.Body>
                </Toast>
            </ToastContainer>
        </>
    );
}

export default FeedbackListPage;

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
