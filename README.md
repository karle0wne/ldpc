# LDPC project

[![pb.png](https://bitbucket.org/repo/7EgkgLG/images/3909742634-pb.png)](http://ag-solutions.ru/)

# Последнее обновление

  - отрефакторен весь проект
  - добавлены новые зависимости в проект
  - добавлены функции траснпонирования матрицы и перемножения
  - также добавлен дополнительный функционал для работы с матрицами
  - подготовлены тестовые матрицы
  - развернут стенд для демонстрации цепочки кодирования\декодирования

### Использование

Что бы воспользоваться программой, необходимо открыть файл *MainApplicationTest* в *IDEA* а нажать кнопочку запустить :)
```java
BooleanMatrix informationWord = booleanMatrixService.createPrepared2InformationWord();
        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        booleanMatrixService.print(informationWord);
        System.out.println("---------------");

        GeneratingMatrix generatingMatrix = generatingMatrixService.createPrepared2GeneratingMatrix();
        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА:");
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());
        System.out.println("---------------");

        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.createPrepared2ParityCheckMatrix();
        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА:");
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ КОДОВОГО СЛОВА");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("КОДОВОЕ СЛОВО: ");
        booleanMatrixService.print(codeWord);
        System.out.println("---------------");

        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ СИНДРОМА");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        System.out.println("---------------");

        System.out.println("СИНДРОМ: ");
        booleanMatrixService.print(syndrome);
        System.out.println("---------------");
```

Здесь реализована вся цепочка простейшего кодирования\декодирования, при этом используются готовые: 
  - *G*
  - *H*
  - *информационное слово*

# Дальнейший план действий

Свести задачу к заданию только *H* и *информационного слова* без *G*
