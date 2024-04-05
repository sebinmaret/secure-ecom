import React from 'react';
import { useContext } from 'react';
import { Button, Col, Container, Form, Modal, Nav, NavDropdown, Navbar, Row, Toast, ToastContainer } from 'react-bootstrap';
import { AuthConsumer } from '../providers/authProvider';
import { BsCart, BsCart4 } from "react-icons/bs";
import { useState } from 'react';
import axios from 'axios';
import { isAlpha, isAlphanumeric, isAscii, isCurrency, isFloat, isLength } from 'validator';
import isURL from 'validator/lib/isURL';

export const AdminNavbar = () => {
    const auth = useContext(AuthConsumer);
    const headers = {
        headers: { Authorization: `Bearer ${auth.getAccessToken()}` }
    };

    const [showModal, setShowModal] = useState(false);
    const handleClose = () => setShowModal(false);
    const handleShow = () => {
        clearAddProductForm();
        setShowModal(true);
        setValidated(false);
    }



    const [showToast, setShowToast] = useState(false);
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });




    const [addProductTrigger, setAddProductTrigger] = useState(false);
    const [validated, setValidated] = useState(false);
    const [addProductFormData, setAddProductFormData] = useState({
        name: "",
        category: "",
        price: 0.0,
        description: "",
        imageUrl: "",
    });


    const validateProductName = (name) => {
        return (isAlphanumeric(name, "en-US", { ignore: " -'" }) && isLength(name, { min: 3, max: 30 }) && isAscii(name));
    }

    const validateCategory = (name) => {
        return (isAlpha(name, "en-US", { ignore: " -" }) && isLength(name, { min: 3, max: 30 }) && isAscii(name));
    }

    const validatePrice = (price) => {
        return (isFloat(price.toString(), { min: 0.0, max: 100000.0 }) && isAscii(price.toString()));
    }

    const validateDescription = (description) => {
        return (isAlphanumeric(description, "en-US", { ignore: " .,'`~-%!?" }) && isLength(description, { min: 3, max: 300 }) && isAscii(description));
    }

    const validateImageURL = (url) => {
        return (isURL(url, { protocols: ['http', 'https', 'ftp'], require_tld: true, require_protocol: true, require_host: true, require_port: false, require_valid_protocol: true, allow_underscores: false, host_whitelist: false, host_blacklist: false, allow_trailing_dot: false, allow_protocol_relative_urls: false, allow_fragments: true, allow_query_components: true, disallow_auth: false, validate_length: true }) && isAscii(url));
    }


    const submitFn = (event) => {

        const form = event.currentTarget;
        console.log(form.checkValidity())
        console.log(form)

        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (validateProductName(addProductFormData.name) && validateCategory(addProductFormData.category) && validateDescription(addProductFormData.description) && validatePrice(addProductFormData.price) && validateImageURL(addProductFormData.imageUrl)) {
            setAddProductTrigger(true);
        }
        setValidated(true);
        event.preventDefault();


    };
    const onChange = (event) => {
        setValidated(false);
        const { name, value } = event.target;
        setAddProductFormData({
            ...addProductFormData,
            [name]: value,
        });
    };


    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };

    const clearAddProductForm = () => {
        setAddProductFormData({
            name: "",
            category: "",
            price: 0.0,
            description: "",
            imageUrl: "",
        });
    };

    console.log("valliid"+validatePrice(addProductFormData.price))


    React.useEffect(() => {
        if (addProductTrigger) {
            axios.post("http://localhost:8080/product", addProductFormData, headers).then((response) => {
                console.log(response);
                createToast({ title: "Product Created", body: "Product is created successfully.", type: "light" });
                setAddProductTrigger(false);
                handleClose();

            }).catch((error) => {
                console.log(error);
                if(error.response.status===401){
                    auth.signinRedirect();
                }
                createToast({ title: "Product Not Created", body: error.response.data.message, type: "danger" });
                setAddProductTrigger(false);


            });
        }

    }, [addProductTrigger]);

    return (
        <>
            <ToastContainer position="top-end" className="pt-5 mt-4 px-3" style={{ zIndex: 100000 }}>
                <Toast bg={toastData.type} onClose={() => setShowToast(false)} show={showToast} delay={3000} autohide >
                    <Toast.Header>
                        <strong className="me-auto">{toastData.title}</strong>
                        <small>Now</small>
                    </Toast.Header>
                    <Toast.Body>{toastData.body}</Toast.Body>
                </Toast>
            </ToastContainer>

            <Container className='py-3 m-0 bg-dark text-white border-bottom border-2 rounded-bottom-3' fluid>
                <Row className=' row mx-0 p-0 justify-content-between text-white'>
                    <div className='text-white col-auto me-auto pl-1 py-2' >
                        {/* <Navbar.Brand href="/">E Commerce</Navbar.Brand> */}
                        <div className='d-inline mx-4 ' ><a className='text-white text-decoration-none fs-3 fw-light' href='/'  >Cartopia</a></div>
                        <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='/'  >Home</a></div>
                        <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='/feedback' >Contact</a></div>
                        {/* <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='#'  >About</a></div> */}
                        {auth.isAuthenticated() && auth.parseJwt(auth.getAccessToken())['roles'][0] === 'ADMIN' ? (
                            <>
                            <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='#' onClick={handleShow} > Add Product</a></div>
                            <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='/feedbacklist' >Feedback List</a></div>
                            </>
                        ) : (
                            <>
                            </>
                        )}
                    </div>
                    <div className='text-light col-auto mr-0'>
                        {auth.isAuthenticated() && auth.parseJwt(auth.getAccessToken())['roles'][0] === 'ADMIN' ? (
                            <div className='d-inline-block  fs-6 text-secondary px-3 py-2 border-end border-2 border-secondary fw-bold'>
                                Signed in as Admin: <div className='text-light d-inline-block fw-bold fs-6 ' > {auth.getUsername()} </div>
                            </div>
                        ) : (
                            <div className='d-inline-block  fs-6 text-secondary px-3 py-2 border-end border-2 border-secondary fw-bold'>
                                Signed in as: <div className='text-light d-inline-block fw-bold fs-6 mx-2' > Guest </div>
                            </div>
                        )}

                        <div className="d-inline fs-6 mx-3">
                            {/* <a className='text-muted text-decoration-none' href='/' ><div className='d-inline mx-1' >Login </div></a> */}
                            {auth.isAuthenticated() ? (
                                <>
                                    {/* <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none py-4 fs-3' href='/cart'  ><BsCart4 /></a></div> */}
                                    <div className='d-inline mx-3' ><a className='text-light text-decoration-none py-4' href='/changepassword' >Change Password</a></div>
                                    <div className='d-inline mx-2 ' ><a className='text-light text-decoration-none py-4' href='#' onClick={() => auth.logout()} >Logout</a></div>
                                </>
                            ) : (
                                <>
                                    <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none py-4' href='#' onClick={() => auth.signinRedirect()} >Login</a></div>
                                    <div className='d-inline mx-2 ' ><a className='text-light text-decoration-none py-4' href='#' onClick={handleShow}>Sign Up</a></div>
                                </>
                            )}
                        </div>
                    </div>
                </Row>
            </Container>
            {/* name: "",
        category: "",
            : 0.0,
        description: "",
        imageUrl: "", */}
            <Modal size="lg" show={showModal} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Add New Product</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form noValidate onSubmit={submitFn} >
                        <Row className="mb-3">
                            <Form.Group as={Col} md="4" controlId="validationCustom01">
                                <Form.Label>Product Name</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="Product Name"
                                    name="name"
                                    value={addProductFormData.name}
                                    onChange={onChange}
                                    minLength={3}
                                    maxLength={30}
                                    isInvalid={
                                        validated && !validateProductName(addProductFormData.name)
                                    }

                                />
                                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">
                                    Please choose a valid Product Name.
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md="4" controlId="validationCustom02">
                                <Form.Label>Product Category</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="Product Category"
                                    name="category"
                                    value={addProductFormData.category}
                                    onChange={onChange}
                                    minLength={3}
                                    maxLength={30}
                                    isInvalid={
                                        validated && !validateCategory(addProductFormData.category)
                                    }
                                />
                                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">
                                    Please choose a valid Category.
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md="4" controlId="validationCustomUsername">
                                <Form.Label>Product Price</Form.Label>
                                <Form.Control
                                    required
                                    type='number'
                                    step="0.01"
                                    min='0'
                                    max='100000'
                                    placeholder="Product Price"
                                    name="price"
                                    value={addProductFormData.price}
                                    onChange={onChange}
                                    isInvalid={
                                        validated && !validatePrice(addProductFormData.price)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please choose a valid Price.
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Row>
                        <Row className="mb-3">

                            <Form.Group as={Col} controlId="formGridEmail">
                                <Form.Label>Description</Form.Label>
                                <Form.Control
                                    required
                                    type="texrarea"
                                    placeholder="Enter Product Description"
                                    name="description"
                                    value={addProductFormData.description}
                                    onChange={onChange}
                                    minLength={6}
                                    maxLength={300}
                                    isInvalid={
                                        validated && !validateDescription(addProductFormData.description)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide a valid description between 6-300 characters, contianing alphanumeric and .,'`~-%!? charachters.
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mt-3" controlId="password">
                                <Form.Label>Image Url</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="imageUrl"
                                    value={addProductFormData.imageUrl}
                                    placeholder="Enter Image URL"
                                    minLength={6}
                                    onChange={onChange}
                                    required
                                    isInvalid={
                                        validated && !validateImageURL(addProductFormData.imageUrl)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide a valid image url.
                                </Form.Control.Feedback>
                            </Form.Group>

                        </Row>
                        <Row className='d-flex flex-row justify-content-end mx-2'>

                            <Button className='d-inline col-2 mx-3' variant="secondary" onClick={handleClose}>
                                Close
                            </Button>
                            <Button className='d-inline col-2' type='submit'>Submit form</Button>
                        </Row>
                    </Form>
                </Modal.Body>

            </Modal>
        </>
    );
};