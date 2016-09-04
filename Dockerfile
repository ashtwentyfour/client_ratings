FROM ashtwentyfour/java-node

RUN mkdir -p /code

COPY package.json /code/package.json

COPY server.js /code/server.js

COPY rating_system /code/rating_system

RUN cd /code && npm install

WORKDIR /code

EXPOSE 8081

ENTRYPOINT ["npm", "start"]
