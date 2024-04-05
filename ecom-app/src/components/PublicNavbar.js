import React from 'react';
import { useContext } from 'react';
import { Button, Col, Container, Form, Modal, Nav, NavDropdown, Navbar, Row, Toast, ToastContainer } from 'react-bootstrap';
import { AuthConsumer } from '../providers/authProvider';
import { BsCart, BsCart4 } from "react-icons/bs";
import { useState } from 'react';
import axios from 'axios';
import { isAlpha, isAlphanumeric, isAscii, isLength } from 'validator';
import isEmail from 'validator/lib/isEmail';

export const PublicNavbar = () => {
    const auth = useContext(AuthConsumer)
    const [showModal, setShowModal] = useState(false);
    const [showToast, setShowToast] = useState(false);
    const [validated, setValidated] = useState(false);
    const [createUserTrigger, setCreateUserTrigger] = useState(false);

    const [registrationFormData, setRegistrationFormData] = useState({
        username: "",
        password: "",
        confirmPassword: "",
        email: "",
        firstName: "",
        lastName: "",
    });
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });

    const validateUsername = (name) => {
        const re = /^[A-Za-z][A-Za-z0-9_]{4,34}$/;
        const match = name.match(re);
        let regmatch = false;
        if (!match) {
            regmatch = false;
        }
        else {
            regmatch = true;
        }
        return (isAlphanumeric(name, "en-US", { ignore: " _" }) && isLength(name, { min: 5, max: 35 }) && isAscii(name) && regmatch);
    };

    const validateFirstName = (name) => {
        return (isAlpha(name, "en-US", { ignore: " .'`~-" }) && isLength(name, { min: 3, max: 35 }) && isAscii(name));
    };

    const validateLastName = (name) => {
        return (isAlpha(name, "en-US", { ignore: " .'`~-" }) && isLength(name, { min: 3, max: 35 }) && isAscii(name));
    };

    const validatePassword = (password) => {
        return (isLength(password, { min: 8, max: 64 }));
    }

    const validatePasswordMatch = (password, confimPassword) => {
        return (password === confimPassword);
    }

    const validateEmail = (email) => {
        return (isEmail(email) && isLength(email, { min: 6, max: 64 }) && isAscii(email));
    }





    const handleClose = () => setShowModal(false);
    const handleShow = () => {
        clearRegistrationForm();
        setShowModal(true);
        setValidated(false);
    }
    const submitFn = (event) => {

        const form = event.currentTarget;
        console.log(form.checkValidity())
        console.log(form)

        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (validateUsername(registrationFormData.username)
            && validateFirstName(registrationFormData.firstName)
            && validateLastName(registrationFormData.lastName)
            && validateEmail(registrationFormData.email)
            && validatePassword(registrationFormData.password)
            && validatePasswordMatch(registrationFormData.password, registrationFormData.confirmPassword)) {
            setCreateUserTrigger(true);
        }
        setValidated(true);
        event.preventDefault();


    };
    const onChange = (event) => {
        setValidated(false);
        const { name, value } = event.target;
        setRegistrationFormData({
            ...registrationFormData,
            [name]: value,
        });
    };
    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };
    const clearRegistrationForm = () => {
        setRegistrationFormData({
            username: "",
            password: "",
            confimPassword: "",
            email: "",
            firstName: "",
            lastName: "",
        });
    };


    React.useEffect(() => {
        if (createUserTrigger) {
            axios.post("http://localhost:9000/user", registrationFormData).then((response) => {
                console.log(response);
                createToast({ title: "User Created", body: " User " + registrationFormData.username + " is created successfully. Login to access the user account.", type: "light" });
                handleClose();
                setCreateUserTrigger(false);

            }).catch((error) => {
                console.log(error);
                if(error.response.status===401){
                    auth.signinRedirect();
                }
                createToast({ title: "User Not Created", body: error.response.data.message, type: "light" });
                setCreateUserTrigger(false);
            });
        }

    }, [createUserTrigger]);



    return (
        <>
            <ToastContainer position="top-end" className="pt-5 mt-4 px-3" style={{ zIndex: 100000 }}>
                <Toast bg={toastData.type} onClose={() => setShowToast(false)} show={showToast} delay={5000} autohide >
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
                        <div className='d-inline mx-3 ' ><a className='text-light text-decoration-none ' href='#'  >About</a></div>
                    </div>
                    <div className='text-light col-auto mr-0'>
                        {auth.isAuthenticated() ? (
                            <div className='d-inline-block  fs-6 text-secondary px-3 py-2 border-end border-2 border-secondary fw-bold'>
                                Signed in as: <div className='text-light d-inline-block fw-bold fs-6 ' > {auth.getUsername()} </div>
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
                                    <div className='d-inline mx-2' ><a className='text-light text-decoration-none py-4 fs-3' href='/cart'  ><BsCart4 /></a></div>
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



            <Modal size="lg" show={showModal} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Create New User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form noValidate onSubmit={submitFn} >
                        <Row className="mb-3">
                            <Form.Group as={Col} md="6" controlId="validationCustom01">
                                <Form.Label>First name</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="First name"
                                    name="firstName"
                                    value={registrationFormData.firstName}
                                    onChange={onChange}
                                    minLength={3}
                                    maxLength={35}
                                    isInvalid={
                                        validated && !validateFirstName(registrationFormData.firstName)
                                    }
                                />
                                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">
                                    First Name should be between 3-35 characters and can only contain alphabets and .'`~-
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md="6" controlId="validationCustom02">
                                <Form.Label>Last name</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="Last name"
                                    name="lastName"
                                    value={registrationFormData.lastName}
                                    onChange={onChange}
                                    minLength={3}
                                    maxLength={35}
                                    isInvalid={
                                        validated && !validateLastName(registrationFormData.lastName)
                                    }
                                />
                                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">
                                    Last Name should be between 3-35 characters and can only contain alphabets and .'`~-
                                </Form.Control.Feedback>

                            </Form.Group>

                        </Row>
                        <Row className="mb-3">
                            <Form.Group controlId="validationCustomUsername">
                                <Form.Label>Username</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="Username"
                                    name="username"
                                    value={registrationFormData.username}
                                    onChange={onChange}
                                    minLength={5}
                                    maxLength={35}
                                    isInvalid={
                                        validated && !validateUsername(registrationFormData.username)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Username should be between 5-35 characters and can only contain alphanumeric and _
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mt-2" controlId="formGridEmail">
                                <Form.Label>Email</Form.Label>
                                <Form.Control
                                    required
                                    type="email"
                                    placeholder="Enter email"
                                    name="email"
                                    value={registrationFormData.email}
                                    onChange={onChange}
                                    minLength={6}
                                    maxLength={64}
                                    isInvalid={
                                        validated && !validateEmail(registrationFormData.email)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please provide a valid email.
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mt-2" controlId="password">
                                <Form.Label>Password</Form.Label>
                                <Form.Control
                                    type="password"
                                    name="password"
                                    value={registrationFormData.password}
                                    onChange={onChange}
                                    minLength={8}
                                    maxLength={64}
                                    required
                                    isInvalid={
                                        validated && !validatePassword(registrationFormData.password)
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Password must be at least 8 characters long.
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mt-2" controlId="confirmPassword">
                                <Form.Label>Confirm Password</Form.Label>
                                <Form.Control
                                    type="password"
                                    name="confirmPassword"
                                    value={registrationFormData.confirmPassword}
                                    onChange={onChange}
                                    minLength={8}
                                    maxLength={64}
                                    required
                                    pattern={registrationFormData.password}
                                    isInvalid={
                                        validated && (!validatePasswordMatch(registrationFormData.password, registrationFormData.confirmPassword))
                                    }
                                />
                                <Form.Control.Feedback type="invalid">
                                    Passwords do not match.
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
                {/* <Modal.Footer>
                   
                </Modal.Footer> */}
            </Modal>
        </>
    );
};