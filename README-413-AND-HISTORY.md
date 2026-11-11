# Offer Letter Backend – PDF Upload and History Fix

## What changed
- Increased Spring multipart limits to 25 MB per PDF and 27 MB per request (configurable with `MAX_FILE_SIZE` and `MAX_REQUEST_SIZE`).
- Aligned file storage validation to a 25 MB PDF maximum.
- Improved the HTTP 413 response with a clear upload-size explanation.
- Previous offer history supports searching candidate name, recipient email, job title, department, work location, and numeric saved-record ID. Search returns up to 50 matches; `GET /api/offers/{id}` returns full saved details including the stored PDF URL.
- Existing `/files/**` static resource mapping serves saved PDF files from the configured upload directory.

## Required deployment configuration
Set `APP_UPLOAD_DIR` to a persistent writable disk/folder. Set database credentials and allowed frontend origins through the existing environment variables. The platform ingress / reverse proxy must also permit request bodies of at least 27 MB; application properties cannot override a smaller upstream proxy limit. If hosted behind Nginx, configure `client_max_body_size 27m;` and reload Nginx.

## Endpoints
- `POST /api/save-offer` – multipart form with offer fields and `pdf` file.
- `GET /api/offers` – latest 50 saved records.
- `GET /api/search-offers?q=...` – search saved records.
- `GET /api/offers/{id}` – complete saved record.
- `/files/{stored-file-name}` – saved PDF file.

## Build
```bash
mvn clean package
java -jar target/offer-letter-api-1.0.0.jar
```
