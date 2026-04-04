# Spring Boot Security Learning Platform

Welcome! 🎉

This project is an **interactive playground designed specifically for learning Spring Security** and real-world backend security concepts.

If you have **zero coding knowledge** or **zero Java knowledge**, don't worry! This README is designed to guide you step-by-step through setting up, configuring, and testing this security platform.

## What is this?
It's a "mock" enterprise backend system. We've built in various security layers (like a bank might have):
* **Rate Limiting:** Prevents someone from spamming your website.
* **JWT (JSON Web Tokens):** A secure digital "VIP pass" that proves who you are.
* **RBAC (Role-Based Access Control):** Ensures only people with an "Admin" badge can see Admin data.
* **Hardening:** Basic rules that keep browsers safe from hackers.

---

## 🛠️ Step 1: Requirements

Before we run the code, you need a few basic tools installed on your computer.

1. **Java Development Kit (JDK 21):**
   - Think of this as the engine that runs Java code.
   - Download it from [Adoptium](https://adoptium.net/) or use your package manager.
   - To check if you have it, open your terminal (or Command Prompt) and type: `java -version`. It should say `21.x.x`.
2. **Terminal / Command Line:**
   - On Mac: Open the `Terminal` app.
   - On Windows: Open `Command Prompt` or `PowerShell`.
3. **A tool to send web requests (Postman or Curl):**
   - We will use `curl` (built into Mac/Linux, mostly built into modern Windows) to talk to our server.

---

## 🚀 Step 2: How to Run the Code

1. **Open your Terminal.**
2. **Navigate to the project folder:**
   ```bash
   cd path/to/security-learning-platform
   ```
3. **Set the required secret passwords:**
   For security, we don't hardcode passwords. We pass them as environment variables. Run these commands:
   *On Mac/Linux:*
   ```bash
   export JWT_SECRET="413F4428472B4B6250655368566D5970337336763979244226452948404D6351"
   export DB_PASSWORD="my-secure-password"
   ```
   *On Windows (PowerShell):*
   ```powershell
   $env:JWT_SECRET="413F4428472B4B6250655368566D5970337336763979244226452948404D6351"
   $env:DB_PASSWORD="my-secure-password"
   ```
4. **Start the server!**
   *On Mac/Linux:*
   ```bash
   ./mvnw spring-boot:run
   ```
   *On Windows:*
   ```cmd
   mvnw.cmd spring-boot:run
   ```
5. **Wait a few seconds.** When you see a message saying `Started SecurityLearningPlatformApplication`, your server is successfully running on your computer!

---

## ⚙️ Step 3: How to Configure the Code

One of the coolest features of this platform is that it is **modular**. You can turn security features ON or OFF like light switches.

1. Open the file located at: `src/main/resources/application.yml`
2. Look for the `security.modules` section.
3. To turn off Rate Limiting, change `enabled: true` to `enabled: false`.
   ```yaml
   rate-limit:
     enabled: false
   ```
4. **Save the file**, stop your server (press `Ctrl + C` in the terminal), and start it again (`./mvnw spring-boot:run`) to see the changes take effect!

---

## 🎓 Next Steps: The Learning Path

Now that the server is running, how do you actually learn from it?

We have prepared a separate **Standard Operating Procedure (SOP)** document. It will give you simple, copy-paste commands to act like a user (or a hacker!) trying to access the system.

👉 **[Click here to open the SOP_GUIDE.md](./SOP_GUIDE.md)** to start your interactive lessons!

---

## FAQ / Troubleshooting

* **"Command not found: mvnw"** -> Ensure you are inside the `security-learning-platform` folder.
* **"Address already in use"** -> Another program is using port 8080. Stop the other program or restart your computer.
* **"Fatal error compiling"** -> You might have the wrong Java version installed. Ensure you have Java 21.