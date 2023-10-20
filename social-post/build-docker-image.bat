REM docker build -t kpolyanichko/social-post:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/social-post:latest --push .