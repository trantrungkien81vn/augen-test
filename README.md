AUGEN TEST - TRAN_TRUNG_KIEN

Requirements:

- Maven
- Java 11
- Docker, Docker compose

RUN THE APP

1. Run mvn clean install
2. Run docker-compose up

API

- GET /pricing?a=[btcAmount]&c=[currency]

Return the total price in specific curreny for amount of bitcoin. Default a=1 and c="NZD" if not supplied

- GET /pricing/data  

It is used to debug data stored in store state of kafa

- GET /pricing/btc??a=[btcAmount]&c=[currency]

Return amount of bitcoin for a amout of money in specific currency.Default curreny is NZD if not supplied

TIMETABLE

- Prepare and setup enviroment and project: 3 hours
- Stuyding Kafka stream: 8 hours (i have not worked with kafka streaming before)
- Code implementation: 8 hours
- Testing: 4 hours