REM docker build -t kpolyanichko/social-dialog:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/social-dialog:latest --push .