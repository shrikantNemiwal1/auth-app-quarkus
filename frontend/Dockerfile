# Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Install deps
COPY package*.json ./
RUN npm ci

# Copy source and build
COPY . .
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL
# Vite build outputs to /app/dist by default
RUN npm run build

# Runtime stage
FROM nginx:alpine
RUN apk add --no-cache curl

# Copy Vite build output
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx config within same context
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
