## Идея

https://sysout.ru/vvedenie-v-spring-batch-job-jobinstance-executioncontext-joblauncher-i-restart/

## Задача проекта

Наша задача — считать данные из файла input.csv, 
преобразовать название животного в верхний регистр и записать результат в файл output.json в JSON-формате:

input.csv

```csv
id,name
1,dog
2,cat
3,fox
4,elephant
5,eagle
6,squirrel
7,tiger
8,shark
```
output.json

```json
[
   {"id":"1","name":"DOG"},
   {"id":"2","name":"CAT"},
   {"id":"3","name":"FOX"},
   {"id":"4","name":"ELEPHANT"},
   {"id":"5","name":"EAGLE"},
   {"id":"6","name":"SQUIRREL"},
   {"id":"7","name":"TIGER"},
   {"id":"8","name":"SHARK"}
]
```

## План

1. Создание болванки проекта spring-batch-csv-to-json-demo
2. Добавление зависимостей
3. Настройка application.yml
   отключим авто-запуск job
   настроим H2
   включим Batch tables
4. Создание модели (POJO) Animal
5. Создание Reader / Processor / Writer
   FlatFileItemReader
   ItemProcessor
   ItemWriter
6. Создание BatchConfig 👉 Job + Step (самое главное)
7. Создание JobRunner (через CommandLineRunner, как ты уже показывал)
8. Тестирование
   нормальный запуск
   потом падение
   потом restart

## Библиотеки

> ✔ spring-boot-starter-batch\
> ✔ spring-boot-starter-jdbc\
> ✔ h2\
> ✔ lombok

✅ Устанавливаем

* Spring Batch 👉 ядро (Job, Step, chunk и т.д.)
* Spring Batch JDBC 👉 хранение метаданных batch (очень важно)
* Spring Data JDBC 👉 подтянулось автоматически — нормально, пусть будет
* H2 👉 наша “временная БД” для демо

## Properties

application.properties:

```aiignore
spring.application.name=demo-spring-batch-csv-to-json

spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always

spring.datasource.url=jdbc:h2:mem:batchdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
```

## Создаем packages

```aiignore
fr.viamedis.demo.springbatch
├── config
├── model
├── processor
└── runner
```

И пока пустые классы:

```aiignore
config/BatchConfig.java
model/Animal.java
processor/AnimalProcessor.java
runner/JobRunner.java
```

## Пояснения

✅ Если нет @Bean

👉 Ты сам создаёшь объект:

```aiignore
new FlatFileItemReader<>();
```

И:

> * каждый раз новый объект
> * сам управляешь жизнью объекта
> * Spring про него ничего не знает

✅ Если есть @Bean

👉 Spring делает это за тебя:

> * создаёт объект один раз (по умолчанию singleton)
> * хранит его внутри контейнера
> * вставляет (inject) туда, где он нужен

🔥 Идеальная формулировка (запомни)

> 👉
> @Bean = “пусть Spring создаёт и раздаёт этот объект”

Пример использования @Bean:

```java
// Объявляем Bean
@Bean
public FlatFileItemReader<Animal> reader() { 
    // some code
}
```

```java
// Использование Bean: объект FlatFileItemReader<Animal> reader
@Bean
public Step step(FlatFileItemReader<Animal> reader) {
    // some code
}
```