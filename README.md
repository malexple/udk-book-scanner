# udk-book-scanner
Find UDK number in pdf and djvu documents

```shell
java -jar .\jar\udk-searcher-jar-with-dependencies.jar
Необходимо указать все параметры: folder, format и csv.
usage: UDKSearcher
-csv <arg>      Имя csv файла для записи результатов
-folder <arg>   Путь к папке для поиска файлов
-format <arg>   Формат файлов для поиска (например, pdf, djvu)
```

```shell
java -jar .\jar\udk-searcher-jar-with-dependencies.jar -format pdf,djvu -folder "d:\library\" -csv books.csv
Результаты записаны в файл: books.csv
Прогресс: 0,65%
```