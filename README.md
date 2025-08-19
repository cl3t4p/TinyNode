# TinyNode

TinyNode is a lightweight, secure backend server designed to manage and communicate with connected devices. It provides
a RESTful API for control and a WebSocket interface for real-time, bidirectional communication with the devices
themselves. Security is a core principle, with communications encrypted using modern cryptographic libraries.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [Building from Source](#building-from-source)

## Features

- **Device Management**: Register and manage devices via a persistent SQLite database.
- **RESTful API**: Control devices and retrieve information using a simple JSON-based API.
- **Secure WebSocket Communication**: Devices connect via a secure WebSocket channel for real-time command and data
  exchange.
- **End-to-End Encryption**: Commands sent to devices are encrypted using a per-device key (ChaCha20-Poly1305).
- **Configuration-driven**: Server settings, database connections, and keys are managed through a simple `config.json`
  file.

## Architecture

TinyNode is built with Java 21 and leverages several key technologies:

- **Web Framework**: [Javalin](https://javalin.io/) is used for creating both the REST API and WebSocket handlers due to
  its simplicity and low overhead.
- **Database**: [SQLite](https://www.sqlite.org/) for lightweight, file-based data persistence.
- **Cryptography**: [LazySodium](https://github.com/terl/lazysodium-java) provides a high-level API for the Libsodium
  cryptographic library, ensuring robust and secure encryption.
- **Build System**: [Apache Maven](https://maven.apache.org/) manages dependencies and the build lifecycle.

The application is structured into several layers:

- **Routes**: Handle incoming HTTP and WebSocket requests (`APIHandler`, `WSDeviceHandler`).
- **Database**: A repository pattern (`DeviceRepo`, `DeviceRepoSQLite`) abstracts database interactions.
- **Models**: Plain Old Java Objects (POJOs) like `BaseDevice` and `CommandRequest` represent the core data structures.
- **Tools**: Utility classes for handling cryptography (`AESTools`), session management (`MirroredSession`), and
  configuration.

## API Endpoints

All API endpoints are prefixed with `/api/v01`.

### REST API

- **`GET /device/com/devices`**
    - **Description**: Retrieves a list of all registered devices.
    - **Response**: `200 OK` with a JSON array of device objects.

- **`POST /device/command`**
    - **Description**: Sends a command to a connected device. The device must be connected to the WebSocket server.
    - **Request Body**:
        ```json
        {
            "deviceId": "your_device_id",
            "command": "type_of_command",
            "commandData": "optional_data"
        }
        ```
    - **Response**: `200 OK` on success, `404 Not Found` if the device is not registered or not connected.

### WebSocket API

- **`WS /device/com`**
    - **Description**: The primary endpoint for devices to establish a persistent connection with the server.
    - **Authentication**: A device must present a `Cookie` header with a `code` value during the handshake. This `code`
      is the device's unique ID.
        ```
        Cookie: code=your_device_id
        ```
    - **Communication**: Once connected, the server can send encrypted binary command packets to the device.

## Security

- **Shared Key**: The `config.json` file contains a `shared_key`. This key is intended for initial authentication or
  encrypting globally relevant information.
- **Per-Device Keys**: Each device registered in the database has its own unique 32-byte private key. All commands sent
  to a device are encrypted with its specific key, ensuring that only the target device can decrypt the payload.
- **Authentication**: WebSocket connections are authenticated by checking the `code` cookie against the device IDs in
  the database. Unregistered devices are disconnected immediately.

## Prerequisites

- Java 21 or later
- Apache Maven 3.8.x or later

## Setup and Installation

1. **Clone the repository:**
   ```sh
   git clone <repository-url>
   cd TinyNode
   ```

2. **Initial Configuration**:
   The application requires a `config/config.json` file to run. On the first launch, it will automatically copy a
   template from its internal resources.

       The default `config.json` looks like this:
       ```json
       {
           "ip": "0.0.0.0",
           "port": 7002,
           "ip_grabber": "https://api4.ipify.org/",
           "shared_key": "wlms2lN4So1Z2ULUnC5Eyil6iafA9W1d+C7Hy0OvwTk=",
           "db": {
               "db_type": "SQLite",
               "url": "jdbc:sqlite:data/sqlite.db"
           }
       }
       ```
       - You can modify the `port` and `shared_key`. The `shared_key` must be a Base64-encoded 32-byte key.

## Running the Application

You can run the application directly through Maven or by building an executable JAR.

**Using Maven:**

```sh
mvn compile exec:java -Dexec.mainClass="com.cl3t4p.TinyNode.TinyNode"
```

The server will start on the host and port specified in `config/config.json`. The `data/` and `config/` directories will
be created in the project root if they don't exist.

## Building from Source

To create a single, executable JAR file, use the `maven-shade-plugin` configured in the `pom.xml`.

1. **Package the application:**
   ```sh
   mvn package
   ```

2. **Run the JAR file:**
   The output JAR will be located in the `target/` directory.
   ```sh
   java -jar target/TinyNode-1.0.jar
   ```