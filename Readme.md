# Dummy language

В задании требовалось реализовать различные виды статического анализа для
языка *Dummy language*.

Данное решение включает в себя 5 видов статического анализа.
Для каждого из них реализован *visitor* дерева разбора программы,
а также *checker*, использующий *visitor* для проверки дерева на содержание
ошибок. Для каждого вида статического анализа также реализовано исключение.
При обнаружении ошибки, *visitor* выбрасывает соответствующее исключение, которое
отлавливается в *cheker'е*, после чего в выходной поток записывается сообщение
о найденной ошибке. Метод *analyze* класса [DummyLanguageAnalyzer](src/main/kotlin/org/jetbrains/dummy/lang/DummyLanguageAnalyzer.kt)
применяет все реализованные виды статического анализа к входному файлу.

**Список реализованных видов статического анализа:**
* **Variable initialization checker**  
Проверка того, что ни к одной переменной не было обращения до того,
как она была инициализирована. При этом переменная считается инициализированной,
если она была инициализирована на всех путях исполнения функции, которые могли привести
к рассматриваемому обращению к переменной.
    * *Checker* - [VariableInitializationChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/VariableInitializationChecker.kt)
    * *Visitor* - [VariableInitializationVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/VariableInitializationVisitor.kt)
    * *Exception* - [AccessBeforeInitializationException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/AccessBeforeInitializationException.kt)
* **Unreachable code checker**  
Проверка того, что в программе отсутствует код, идущий после возврата из функции.
Например, после использования `return` или после блока `if-else`, в обеих ветвях которого
использовался `return` (то есть в любом случае исполнение завершится в этом блоке).
    * *Checker* - [UnreachableCodeChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/UnreachableCodeChecker.kt)
    * *Visitor* - [UnreachableCodeVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/UnreachableCodeVisitor.kt)
    * *Exception* - [UnreachableCodeException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/UnreachableCodeException.kt)
* **Conflicting declarations checker**  
Проверка того, что в функции ни одна переменная не объявляется повторно. Проверка также запрещает
объявление в теле функции переменной, имя которой совпадает с именем какого-либо параметра данной функции.
    * *Checker* - [ConflictingDeclarationsChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/ConflictingDeclarationsChecker.kt)
    * *Visitor* - [ConflictingDeclarationsVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/ConflictingDeclarationsVisitor.kt)
    * *Exception* - [ConflictingDeclarationsException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/ConflictingDeclarationsException.kt)
* **Wrong arguments checker**  
Проверка того, что ни одна функция не вызывается с неправильным количеством аргументов.
То есть если в объявлении функции указано *n* параметров, то вызвать ее можно только
с *n* параметрами, иначе данная проверка выбросит исключение.
    * *Checker* - [WrongArgumentsChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/WrongArgumentsChecker.kt)
    * *Visitor* - [WrongArgumentsVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/WrongArgumentsVisitor.kt)
    * *Exception* - [WrongArgumentsException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/WrongArgumentsException.kt)
* **Empty body checker**  
Проверка того, что в программе нет пустых блоков кода. Данная проверка
выбросит исключение, если встретит функцию, блок `if` или `else`
с пустым телом.
    * *Checker* - [EmptyBodyChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/EmptyBodyChecker.kt)
    * *Visitor* - [EmptyBodyVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/EmptyBodyVisitor.kt)
    * *Exception* - [EmptyBodyException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/EmptyBodyException.kt)

**Тесты**

Класс [DummyLanguageTestGenerated](src/test/kotlin/org/jetbrains/dummy/lang/DummyLanguageTestGenerated.kt)
содержит тесты приведенных видов статического анализа. Тесты были сгенерированы из
файлов в папке [testData](testData).

Тесты включают в себя:
* 5 программ на языке *Dummy language*, не содержащих ошибок
* 10 программ на языке *Dummy language*, содержащих ошибки, которые
отлавливаются *checker'ами*