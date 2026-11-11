# Offer Letter Backend — Render deployment fix

## Why the current Render deployment fails
The log shows Render runs `yarn` and then runs a start command literally equal to `0`.
This is a Java Spring Boot project (`pom.xml`), not a Node/Yarn application. The service is
configured with the wrong runtime/build/start commands (or the wrong Root Directory).

## Deploy with Render Docker runtime (recommended)
1. Put the contents of this folder in the GitHub repository root, so `Dockerfile`, `pom.xml`
   and `src/` are at the same level. Alternatively set Render Root Directory to the folder
   that contains this Dockerfile and pom.xml.
2. In Render create/edit a **Web Service**, select the repository and choose **Docker** runtime.
3. Set Dockerfile Path to `./Dockerfile`. Do not use `yarn` as Build Command.
4. Clear the old Start Command. Dockerfile supplies the start command; do not enter `0`.
5. Add environment variables:
   - `DB_URL` = `jdbc:mysql://<DB_HOST>:3306/<DB_NAME>?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USERNAME` = database username
   - `DB_PASSWORD` = database password
   - `MAIL_USERNAME` = SMTP sender email
   - `MAIL_PASSWORD` = SMTP app password (not normal account password)
   - `APP_ALLOWED_ORIGINS` = `https://<your-frontend>.vercel.app`
   - optionally `JPA_DDL_AUTO` = `update`
6. Ensure the MySQL database accepts connections from Render. Use the provider's external/private
   connection string as appropriate. Never use `localhost` for a separately hosted database.
7. Deploy. Confirm logs show the Spring Boot application started and listening on Render's `$PORT`.
8. In the frontend, set its API base URL to the Render service URL, then redeploy Vercel.

## Security
The original application.properties contained database and Gmail credentials. Rotate those
credentials immediately and only store replacements in Render environment variables.
Do not commit secrets to GitHub.

## Important
This Dockerfile expects `pom.xml` and `src/` in the Render service root. If your GitHub repository
has them inside `offerletter/`, set Render Root Directory to `offerletter` (or move the project
contents to repository root). Keep the Dockerfile alongside pom.xml.
