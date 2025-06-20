FROM nginx:latest

# 기존 설정 제거 (선택적)
RUN rm /etc/nginx/nginx.conf

# 커스텀 nginx.conf 복사
COPY ./nginx.conf /etc/nginx/nginx.conf

