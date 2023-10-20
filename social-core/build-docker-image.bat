REM docker build -t kpolyanichko/social-core:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/social-core:latest --push .