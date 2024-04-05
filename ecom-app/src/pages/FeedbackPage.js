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


function FeedbackPage() {
    const auth = useContext(AuthConsumer);
    const [feedbackForm, setFeedbackForm] = useState({
        name: "",
        email: "",
        description: ""
    });
    const [validated, setValidated] = useState(false);
    const [createFeedbackTrigger, setCreateFeedbackTrigger] = useState(false);
    const [showToast, setShowToast] = useState(false);
    const [toastData, setToastData] = useState({
        type: "Dark",
        title: "",
        body: "",
    });

    const validateName = (name) => {
        return (isAlpha(name, "en-US", { ignore: " .'`~-" }) && isLength(name, { min: 3, max: 75 }) && isAscii(name));
    }

    const validateEmail = (email) => {
        return (isEmail(email) && isLength(email, { min: 6, max: 64 }) && isAscii(email));
    }

    const validateDescription = (description) => {
        return (isAlphanumeric(description, "en-US", { ignore: " .,'`~-_!?$%()" }) && isLength(description, { min: 5, max: 300 }) && isAscii(description));
    }

    const submitFn = (event) => {

        const form = event.currentTarget;
        console.log(form.checkValidity())
        console.log(form)

        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (validateName(feedbackForm.name) && validateEmail(feedbackForm.email) && validateDescription(feedbackForm.description)) {
            setCreateFeedbackTrigger(true);
        }
        setValidated(true);
        event.preventDefault();


    };
    const onChange = (event) => {
        setValidated(false);
        const { name, value } = event.target;
        setFeedbackForm({
            ...feedbackForm,
            [name]: value,
        });
    };
    const createToast = (event) => {
        const { title, body, type } = event;
        setToastData({ title, body, type });
        setShowToast(true);
    };


    console.log("val1:" + validateEmail(feedbackForm.email))

    React.useEffect(() => {
        if (createFeedbackTrigger) {

            axios.post("http://localhost:8080/feedback", feedbackForm).then((response) => {
                console.log(response);
                createToast({ title: "Feedback Submitted", body: "Your feedback has been submitted successfully", type: "light" });
                setCreateFeedbackTrigger(false);
            }).catch((error) => {
                console.log(error);
                if (error.response.status === 401) {
                    auth.signinRedirect();
                }
                createToast({ title: "Feedback Not Submitted", body: error.response.data.message, type: "danger" });
                setCreateFeedbackTrigger(false);
            });
        }
    }, [createFeedbackTrigger]);

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
                <Row xs={1} md={1} lg={1} className='d-flex justify-content-center'>
                    <Col className=' mt-3 w-75' >
                        <Card bg="light" border="dark">
                            <Card.Body>
                                <Card.Title className='row  me-auto'>
                                    <div className=' fs-2'>
                                        Contact Us
                                    </div>
                                </Card.Title>
                                <Card.Subtitle className="mb-2 fs-5 fw-light  text-dark">Have any Questions or Suggestions for us? Let us know !</Card.Subtitle>
                                <Card.Text className='fw-light' >
                                    <Form noValidate onSubmit={submitFn}>
                                        <Row className="mb-3 mt-3">
                                            <Form.Group controlId="validationCustom01">
                                                <Form.Label>Full Name</Form.Label>
                                                <Form.Control
                                                    required
                                                    type="text"
                                                    placeholder="Name"
                                                    name="name"
                                                    value={feedbackForm.name}
                                                    onChange={onChange}
                                                    minLength={5}
                                                    maxLength={75}
                                                    isInvalid={
                                                        validated && !validateName(feedbackForm.name)
                                                    }
                                                />
                                                <Form.Control.Feedback>Looks good!</Form.Control.Feedback>
                                                <Form.Control.Feedback type="invalid">
                                                    Name should be between 5-75 characters and can only contain alphabets and .'`~-
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        </Row>
                                        <Row className="mb-3">
                                            <Form.Group className="mt-2" controlId="formGridEmail" >
                                                <Form.Label>Email</Form.Label>
                                                <Form.Control
                                                    required
                                                    type="email"
                                                    placeholder="name@example.com"
                                                    name="email"
                                                    value={feedbackForm.email}
                                                    onChange={onChange}
                                                    minLength={6}
                                                    maxLength={64}
                                                    isInvalid={
                                                        validated && !validateEmail(feedbackForm.email)
                                                    }
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    Please provide a valid email.
                                                </Form.Control.Feedback>
                                            </Form.Group>

                                        </Row>
                                        <Row className="mb-3 d-flex flex-row">
                                            <Form.Group controlId="vali" validated>
                                                <Form.Label>Suggestions</Form.Label>
                                                <Form.Control

                                                    as="textarea"
                                                    rows={15}
                                                    placeholder="Tell us anything!"
                                                    name="description"
                                                    value={feedbackForm.description}
                                                    onChange={onChange}
                                                    minLength={5}
                                                    maxLength={300}
                                                    isInvalid={
                                                        validated && !validateDescription(feedbackForm.description)
                                                    }
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    Only Alpha-Numeric characters and .'`~-_!?$%() are allowed in description.
                                                </Form.Control.Feedback>
                                            </Form.Group>

                                        </Row>
                                        <Row className='d-flex justify-content-end'>
                                            <Button variant='outline-dark' className='w-25 mx-3' type='submit' >Submit Feedback</Button>
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

export default FeedbackPage;

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
