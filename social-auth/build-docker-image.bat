REM docker build -t kpolyanichko/social-auth:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/social-auth:latest --push .
