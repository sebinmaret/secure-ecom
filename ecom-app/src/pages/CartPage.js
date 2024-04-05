import React, { useState, useEffect } from 'react';
import { PublicNavbar } from '../components/PublicNavbar';
import { Badge, Button, Card, Col, Container, Form, Image, ListGroup, Row, Stack, Toast, ToastContainer } from 'react-bootstrap';
import Product from '../media/product_placeholder.jpg'
import axios from 'axios';
import { CommonFooter } from '../components/CommonFooter';
import { useContext } from 'react';
import { AuthConsumer } from '../providers/authProvider';
import { BsFillTrash3Fill } from "react-icons/bs";
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import * as z from 'zod';
import { isAlpha, isAlphanumeric, isAscii, isCreditCard, isLength, isNumeric } from 'validator';
import { locales } from 'validator/lib/isIBAN';


function CartPage() {


    const auth = useContext(AuthConsumer)
    console.log(auth.isAuthenticated());
    console.log(auth.getAccessToken());
    const headers = {
        headers: { Authorization: `Bearer ${auth.getAccessToken()}` }
    };



    const [removeItem, setRemoveItem] = useState({
        productId: ""
    });
    const [removeItemTrigger, setRemoveItemTrigger] = React.useState(false);


    const [showToast, setShowToast] = useState(false);
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });

    const [cartItemList, setCartItemList] = React.useState([]);
    const [cart, setCart] = React.useState({});
    const [cartRefreshTrigger, setCartRefreshTrigger] = React.useState(false);

    const [validated, setValidated] = useState(false);
    const [checkoutTrigger, setCheckoutTrigger] = useState(false);
    const [paymentFormData, setPaymentFormData] = useState({
        cardHolderName: "",
        cardNumber: "",
        cardExpiry: "",
        cardCVV: "",
    });

    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };


    const validateCardHolderName = (name) => {
        return (isAlpha(name, "en-US", { ignore: " .'`~-" }) && isLength(name, { min: 5, max: 75 }) && isAscii(name));
    };

    const validateCardNumber = (number) => {
        return (isCreditCard(number) && isLength(number, { min: 16, max: 16 }) && isAscii(number));
    };

    const validateCardExpiry = (expiry) => {
        const re = /^(?:0[1-9]|1[0-2])\/(\d{2})$/;
        const match = expiry.match(re);
        let regmatch = false;
        if (!match) {
            regmatch = false;
        }
        else {
            regmatch = true;
        }

        return (isLength(expiry, { min: 5, max: 5 }) && isAscii(expiry) && regmatch);
    };

    const validateCardCVV = (cvv) => {
        const re = /^\d{3}$/;
        const match = cvv.match(re);
        let regmatch = false;
        if (!match) {
            regmatch = false;
        } else {
            regmatch = true;
        }


        return (isLength(cvv, { min: 3, max: 3 }) && isAscii(cvv) && regmatch);
    };


    const submitFn = (event) => {

        const form = event.currentTarget;
        console.log(form.checkValidity())
        console.log(form)

        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (validateCardHolderName(paymentFormData.cardHolderName) && validateCardNumber(paymentFormData.cardNumber) && validateCardCVV(paymentFormData.cardCVV) && validateCardExpiry(paymentFormData.cardExpiry)) {
            setCheckoutTrigger(true);
        }
        setValidated(true);
        event.preventDefault();


    };

    const onChange = (event) => {
        setValidated(false);
        const { name, value } = event.target;
        setPaymentFormData({
            ...paymentFormData,
            [name]: value,
        });
    };

    React.useEffect(() => {
        axios.get("http://localhost:8080/cart", headers).then((response) => {
            setCartItemList(response.data.products)
            setCart(response.data)
            console.log(response.data);

        }).catch((error) => {
            console.log(error)
            if (error.response.status === 401) {
                auth.signinRedirect();
            }
        });
    }, [cartRefreshTrigger]);

    React.useEffect(() => {
        if (removeItemTrigger) {
            axios.delete("http://localhost:8080/cart/item/" + removeItem.productId, headers).then((response) => {
                console.log(response);
                createToast({ title: "Item Removed", body: "Item has been removed from the cart", type: "light" });
                setRemoveItemTrigger(false);
                setCartRefreshTrigger(!cartRefreshTrigger);
            }).catch((error) => {
                console.log(error);
                if (error.response.status === 401) {
                    auth.signinRedirect();
                }
                createToast({ title: "Item Not Removed", body: error.response.data.message, type: "danger" });
                setRemoveItemTrigger(false);
                setCartRefreshTrigger(!cartRefreshTrigger);
            });
        }
    }, [removeItem]);

    React.useEffect(() => {
        if (checkoutTrigger) {
            axios.post("http://localhost:8080/order/checkout", paymentFormData, headers).then((response) => {
                console.log(response);
                createToast({ title: "Cart Check Out", body: response.data.message, type: "light" });
                setPaymentFormData({
                    cardHolderName: "",
                    cardNumber: "",
                    cardExpiry: "",
                    cardCVV: "",
                });
                setValidated(false);
                setCartRefreshTrigger(!cartRefreshTrigger);
                setCheckoutTrigger(false);
            }).catch((error) => {
                console.log(error);
                if (error.response.status === 401) {
                    auth.signinRedirect();
                }
                createToast({ title: "Cart Checkout Unsuccessful", body: error.response.data.message, type: "danger" });
                setValidated(false);
                setCartRefreshTrigger(!cartRefreshTrigger);
                setCheckoutTrigger(false);
            });
        }
    }, [checkoutTrigger]);


    return (
        <>
            {!auth.isAuthenticated() ? auth.signinRedirect() : <></>}
            <PublicNavbar></PublicNavbar>
            <ToastContainer position="top-end" className=" pt-5 mt-4 px-3" style={{ zIndex: 100000 }}>
                <Toast bg={toastData.type} onClose={() => setShowToast(false)} show={showToast} delay={3000} autohide >
                    <Toast.Header>
                        <strong className="me-auto">{toastData.title}</strong>
                        <small>Now</small>
                    </Toast.Header>
                    <Toast.Body>{toastData.body}</Toast.Body>
                </Toast>
            </ToastContainer>
            <Container className='pt-4 h-100 w-100' fluid>
                <Row xs={1} md={2} lg={2}>
                    <Col className='mb-5'>
                        <Row>
                            <div className='mx-5 fs-3'>
                                Cart Items
                            </div>
                        </Row>
                        <Row className='mx-5 mt-4  px-3 py-1  pb-3 border border-secondary'>
                            <Stack>

                                {cartItemList.map((item, idx) => (
                                    <div className='row border-bottom border-1 border-dark p-2' style={{ height: "11em" }} >
                                        <div className='col-3 border d-flex justify-content-center align-items-center'>
                                            <Image src={item['imageUrl']} height={"110"} width={"110"} rounded />
                                        </div>
                                        <div className='d-flex flex-column col-7 '>
                                            <div className=' fs-4 pt-2'>
                                                {item['name']}
                                            </div>
                                            <div className=' fs-5 mx-1 text-muted '>
                                                {item['category']}
                                            </div>
                                            <div className=' fs-5 mx-1 text-muted '>
                                                Qty: 1
                                            </div>
                                            <div className=' fs-5 mx-1 text-muted '>
                                                Price:

                                            </div>


                                        </div>
                                        <div className='col-2  d-flex flex-column justify-content-center align-self-start align-items-center mt-2 p-0 fs-4'>
                                            <Button variant="danger" size='md' className='mt-2 mb-5 fs-5' onClick={() => { console.log(item['id']); setRemoveItemTrigger(true); setRemoveItem({ productId: item['id'] }); }}>
                                                <BsFillTrash3Fill />
                                            </Button>
                                            ${item['price']}
                                        </div>
                                    </div>

                                ))}
                                {cartItemList.length === 0 ? (
                                    <>
                                        <div className='row border-bottom border-2 border-dark p-2' style={{ height: "11em" }} >
                                            <div className='fs-3 d-flex justify-content-center align-items-center' >
                                                Cart is Empty
                                            </div>
                                        </div>
                                    </>
                                ) : (
                                    <></>
                                )}
                                <div className='row' style={{ height: "5em" }}>

                                    <div className='col-9 fs-4 d-flex justify-content-end'>
                                        Cart Total :
                                    </div>
                                    <div className='col-3 fs-4 d-flex justify-content-center '>
                                        $ {cart.cartTotal}
                                    </div>
                                </div>


                            </Stack>

                        </Row>
                    </Col>
                    <Col className='mt-2'>
                        <Row>
                            <div className='mx-5 px-2 fs-3'>
                                Payment Method
                            </div>
                        </Row>
                        <Row className=' mt-3 '>
                            <Col>
                                <div className='px-5 fs-4 fw-light'>
                                    Total Amount to be Paid :
                                </div>
                            </Col>
                            <Col>
                                <div className='mx-5 px-2 fs-4 fw-light'>
                                    $ {cart.cartTotal}
                                </div>
                            </Col>
                        </Row>
                        <Row className='mt-3 mx-5 border p-4 w-75 '>
                            <Form noValidate onSubmit={submitFn}>
                                <Row className="mb-3">
                                    <Form.Group md="4" controlId="validationCustom01">
                                        <Form.Label>Card Holder Name</Form.Label>
                                        <Form.Control
                                            required
                                            type="text"
                                            placeholder="Card Holder Name"
                                            name="cardHolderName"
                                            value={paymentFormData.cardHolderName}
                                            onChange={onChange}
                                            minLength={5}
                                            maxLength={75}
                                            isInvalid={
                                                validated && !validateCardHolderName(paymentFormData.cardHolderName)
                                            }
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            Card Holder Name should be between 5-75 characters and can only contain alphabets, .'`~- and spaces
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Row>
                                <Row className="mb-3">
                                    <Form.Group md="4" controlId="validationCustom02">
                                        <Form.Label>Card Number</Form.Label>
                                        <Form.Control
                                            required
                                            type="text"
                                            placeholder="Card Number"
                                            name="cardNumber"
                                            value={paymentFormData.cardNumber}
                                            onChange={onChange}
                                            minLength={16}
                                            maxLength={16}

                                            isInvalid={
                                                validated && !validateCardNumber(paymentFormData.cardNumber)
                                            }
                                        />
                                        <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                        <Form.Control.Feedback type="invalid">
                                            Please enter a valid Card Number.
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Row>
                                <Row className="mb-3 d-flex flex-row">
                                    <Form.Group as={Col} controlId="validationCustomUsername">
                                        <Form.Label>Card Expiry</Form.Label>
                                        <Form.Control
                                            required
                                            type="text"
                                            placeholder="MM/YY"
                                            name="cardExpiry"
                                            value={paymentFormData.cardExpiry}
                                            onChange={onChange}
                                            minLength={5}
                                            maxLength={5}
                                            isInvalid={
                                                validated && !validateCardExpiry(paymentFormData.cardExpiry)
                                            }
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            Please enter a valid Card Expiry.
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                    <Form.Group as={Col} controlId="formGridEmail">
                                        <Form.Label>Card CVV</Form.Label>
                                        <Form.Control
                                            required
                                            type="text"
                                            placeholder="CVV"
                                            name="cardCVV"
                                            value={paymentFormData.cardCVV}
                                            onChange={onChange}
                                            minLength={3}
                                            maxLength={3}
                                            isInvalid={
                                                validated && !validateCardCVV(paymentFormData.cardCVV)
                                            }
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            Please provide a valid CVV.
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Row>
                                <Row>
                                    <Button variant='outline-success' type='submit' disabled={cartItemList.length === 0} >Checkout Cart</Button>
                                </Row>

                            </Form>

                        </Row>
                    </Col>
                </Row>

            </Container>
            <CommonFooter />

        </>
    );
}

export default CartPage;
