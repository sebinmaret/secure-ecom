import React, { useState, useEffect } from 'react';
import { PublicNavbar } from '../components/PublicNavbar';
import { Button, Card, Col, Container, Form, Row, Toast, ToastContainer } from 'react-bootstrap';
import axios from 'axios';
import { CommonFooter } from '../components/CommonFooter';
import { useContext } from 'react';
import { AuthConsumer } from '../providers/authProvider';
import { BsCartPlus } from "react-icons/bs";
import { AdminNavbar } from '../components/AdminNavbar ';
import { isAlpha, isAlphanumeric, isAscii, isLength } from 'validator';
import isEmail from 'validator/lib/isEmail';


function ChangePasswordPage() {
    const auth = useContext(AuthConsumer);
    const headers = {
        headers: { Authorization: `Bearer ${auth.getAccessToken()}` }
    };


    const [changePasswordForm, setChangePassword] = useState({
        oldPassword: "",
        password: "",
        confirmPassword: ""
    });
    const [validated, setValidated] = useState(false);
    const [apiTrigger, setAPITrigger] = useState(false);
    const [showToast, setShowToast] = useState(false);
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });

    const validatePassword = (password) => {
        return (isLength(password, { min: 8, max: 64 }));
    }

    const validatePasswordMatch = (password, confimPassword) => {
        return (password === confimPassword);
    }

    const submitFn = (event) => {

        const form = event.currentTarget;
        console.log(form.checkValidity())
        console.log(form)

        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (validatePassword(changePasswordForm.oldPassword)
            && validatePassword(changePasswordForm.password)
            && validatePassword(changePasswordForm.confirmPassword)
            && validatePasswordMatch(changePasswordForm.password, changePasswordForm.confirmPassword)) {
            setAPITrigger(true);
        }
        setValidated(true);
        event.preventDefault();


    };
    const onChange = (event) => {
        setValidated(false);
        const { name, value } = event.target;
        setChangePassword({
            ...changePasswordForm,
            [name]: value,
        });
    };
    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };

    React.useEffect(() => {
        if (apiTrigger) {
            axios.post("http://localhost:9000/user/changepassword", changePasswordForm, headers).then((response) => {
                console.log(response);
                createToast({ title: "Password Changed", body: "Password change for your account is successful.", type: "light" });
                setAPITrigger(false);
            }).catch((error) => {
                console.log(error);
                if (error.response.status === 401) {
                    auth.signinRedirect();
                }
                createToast({ title: "Password Not Changed", body: error.response.data.message, type: "danger" });
                setAPITrigger(false);
            });
        }
    }, [apiTrigger]);

    return (
        <>
            {!auth.isAuthenticated() ? auth.signinRedirect() : <></>}
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
            <Container className='pt-4 h-100 w-50' fluid>
                <Row xs={1} md={1} lg={1} className='d-flex justify-content-center'>
                    <Col className=' mt-3 w-75' >
                        <Card bg="light" border="dark">
                            <Card.Body>
                                <Card.Title className='row  me-auto'>
                                    <div className=' fs-2'>
                                        Change Password
                                    </div>
                                </Card.Title>
                                {/* <Card.Subtitle className="mb-2 fs-5 fw-light  text-dark">Have any Questions or Suggestions for us? Let us know !</Card.Subtitle> */}
                                <Card.Text className='fw-light' >
                                    <Form noValidate onSubmit={submitFn}>
                                        <Row className="mb-3 mt-3">
                                            <Form.Group className="mt-2" controlId="password">
                                                <Form.Label>Old Password</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    name="oldPassword"
                                                    value={changePasswordForm.oldPassword}
                                                    onChange={onChange}
                                                    minLength={8}
                                                    maxLength={64}
                                                    required
                                                    isInvalid={
                                                        validated && !validatePassword(changePasswordForm.oldPassword)
                                                    }
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    Password must be at least 8 characters long.
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        </Row>
                                        <Row className="mb-3">
                                            <Form.Group className="mt-2" controlId="password">
                                                <Form.Label>New Password</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    name="password"
                                                    value={changePasswordForm.password}
                                                    onChange={onChange}
                                                    minLength={8}
                                                    maxLength={64}
                                                    required
                                                    isInvalid={
                                                        validated && !validatePassword(changePasswordForm.password)
                                                    }
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    Password must be at least 8 characters long.
                                                </Form.Control.Feedback>
                                            </Form.Group>

                                        </Row>
                                        <Row className="mb-3 d-flex flex-row">
                                            <Form.Group className="mt-2" controlId="confirmPassword">
                                                <Form.Label>Confirm New Password</Form.Label>
                                                <Form.Control
                                                    type="password"
                                                    name="confirmPassword"
                                                    value={changePasswordForm.confirmPassword}
                                                    onChange={onChange}
                                                    minLength={8}
                                                    maxLength={64}
                                                    required
                                                    pattern={changePasswordForm.password}
                                                    isInvalid={
                                                        validated && (!validatePassword(changePasswordForm.confirmPassword) || !validatePasswordMatch(changePasswordForm.password, changePasswordForm.confirmPassword))
                                                    }
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    Passwords do not match.
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        </Row>
                                        <Row className='d-flex justify-content-end'>
                                            <Button variant='outline-dark' className='w-25 mx-3' type='submit' >Change Password</Button>
                                        </Row>

                                    </Form>

                                </Card.Text>

                            </Card.Body>
                        </Card>
                    </Col>

                </Row>

            </Container >
            <CommonFooter />

        </>
    );
}

export default ChangePasswordPage;

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
