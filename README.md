# Energy Management System

## Overview

Welcome to the Energy Management System repository! This project, developed for the Distributed Systems course at the Technical University of Cluj-Napoca, brings together four microservices, a console application, and a front-end. Together, they simulate a system managing smart devices, monitoring their energy consumption and displaying hourly consumption data.

## Table of Contents

1. Conceptual Architecture
    - Overview
    - Microservices
2. Microservices Details
    - User Microservice
    - Device Microservice
    - Device Simulator
    - Monitoring Microservice
    - Chat Microservice
    - Front-end Application
3. Databases
    - User Database
    - Device Database
    - Microservice Database
5. IBM MQ Service
6. Deployment

## Conceptual Architecture

### Overview

The Energy Management System connects users with smart devices, enabling real-time monitoring of energy consumption. Users interact through a React application, triggering operations within microservices. Two user types exist: clients and administrators. Clients view and receive notifications about their devices, while administrators manage clients, devices, and associations.

### Microservices

1. **User Microservice:**
   - Back-end (Spring Boot) for user operations.
   - Uses IBM MQ queues for data exchange with the monitoring microservice.
   - Implements security with Spring Security and JWT tokens.
   - Stores user information (username, encrypted password, role) in a user database.

2. **Device Microservice:**
   - Back-end (Spring Boot) managing device operations.
   - Connects to IBM MQ queues for data exchange with the monitoring microservice.
   - Requires a valid JWT token to authorize any requests made to it. The secret key is shared among all microservices.
   - Stores device information (description, address, max hourly consumption), client-device linkages and client ids to be up to date with the operations made in the user microservice in a device database.

3. **Device Simulator:**
   - Console application simulating devices, sending readings to IBM MQ queues.
   - Supports multiple instances.

4. **Monitoring Microservice:**
   - Back-end (Spring Boot) handling monitoring data requests and notifications.
   - Connects to IBM MQ queues for data exchange with user and device microservices and device simulator.
   - Requires a valid JWT token to authorize any requests in the same way the device microservice does.
   - When a device reaches the maximum hourly consumption, a notification is sent to the client linked to that device via a Web Socket 
   - Stores readings, client-device linkages, and usernames in a monitoring database.

5. **Chat Microservice:**
   - Facilitates bidirectional message redirection between users with the aid of Web Sockets.
   - Implements topics for client and administrator messages.
   - Supports text messages, typing indicators, stopped typing indicators, and seen indicators.

6. **Front-end Application:**
   - React JS application using Axios for HTTP calls and WebSockets for monitoring data.
   - Utilizes JSCharting for displaying monitoring data and implements chat functionality.

## IBM MQ Service

- Manages 5 queues for inter microservice communication:
  - one between the device simulator and the monitoring microservice. The monitoring microservice consumes the messages produced by the device simulator instances (which simulates real time data coming from multiple devices).
  - two between the monitoring microservice and the device microservice. The monitoring microservice produces a message on one queue (it can be called the request queue) that tells the device microservice to send all device data. The device microservice consumes that message and sends n messages on the other queue (it can be called the response queue), each message containing data about a device. Whenever a device is updated/inserted/removed, a message is sent to the monitoring microservice in order to keep the information up to date.
  - two between the monitoring microservice and the user microservice. Works the same as the two queues used between the monitoring and the device microservices.

## Deployment

#### Docker Deployment

The application deploys on Docker, organized into 9 containers:

1. **MySQL Servers Containers:** For user, device, and monitoring databases.
2. **Tomcat Servers Containers:** For Spring Boot microservices (user, device, monitoring and chat microservices).
3. **NGINX Server Container:** For running the React application and routing requests to microservices.
4. **IBM MQ Server Container:** For the IBM MQ queue manager.

For the conceptual architecture diagram and the deployment diagram, refer to the documentation present in the project at the root level.

#### Deployment Instructions

The whole application can be deployed by running `docker-compose up -d` from the project root.
