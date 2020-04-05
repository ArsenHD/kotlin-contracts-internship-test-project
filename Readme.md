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
    
    **Идея алгоритма**  
    *Visitor* обходит дерево разбора, на вход методы *Visitor'а* получают `HashSet<String>`, содержащий
    названия инициализированных переменных. Изначально этот `HashSet` пустой. Когда при обходе
    *Visitor* встречает присвоение переменной значения, он добавляет ее в `HashSet<String>`
    инициализированных переменных. При этом, если он заходит в `ifStatement`, то он
    обходит обе ветви (или одну, если вторая отсутствует), используя для обхода
    копии текущего множества инициализированных переменных. Таким образом, когда обход
    ветвей заканчивается, *Visitor* проверяет, что если в одной из ветвей были проинициализированы
    переменные, то эти же переменные были проинициализированы и в другой. Переменные, для
    которых данное условие выполняется, добавляются во множество инициализированных переменных,
    так как при любом пути исполнения программы, они будут проинициализированы в одной
    из веток. Если `ifStatement` имеет только ветвь `then`, то переменные, проинициализированные внутри этой ветви, не добавляются
    во множество инициализированных переменных, так как программа могла не зайти в эту ветвь `then`.
    Здесь есть одно исключение: если условие `ifStatement'а`
    является константой `true`, то все переменные, инициализированные в блоке `then`
    будут отмечены как инициализированные, так как эта ветвь будет исполняться всегда.
    Аналогично, если условие является константой `false`, то все переменные, инициализированные в блоке `else`
    будут отмечены как инициализированные, так как в этом случае всегда будет исполняться ветвь `else` (если она есть).
      
* **Unreachable code checker**  
Проверка того, что в программе отсутствует код, идущий после возврата из функции.
Например, после использования `return` или после блока `if-else`, в обеих ветвях которого
использовался `return` (то есть в любом случае исполнение завершится в этом блоке).
    * *Checker* - [UnreachableCodeChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/UnreachableCodeChecker.kt)
    * *Visitor* - [UnreachableCodeVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/UnreachableCodeVisitor.kt)
    * *Exception* - [UnreachableCodeException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/UnreachableCodeException.kt)  
    
    **Идея алгоритма**  
    *Visitor* обходит дерево разбора, на вход методы *Visitor'а* получают значение `Boolean`,
    возвращают они так же значение типа `Boolean`. Входной аргумент означает было ли уже достигнуто
    `returnStatement`. Если да, то текущее выражение идет после него, а значит является недостижимым.
    В данном случае выбрасывается исключение. Если нет, то метод возвращает `false`, и это значение
    передается дальнейшим выражениям в блоке кода, сигнализируя о том, что `returnStatement` еще не был достигнут.
    Заходя в `returnStatement`, *Visitor* возвращает `false`. Если в дереве разбора в данном блоке кода
    за этим `returnStatement` есть еще выражения, то им передастся это значение
    `true` и они выбросят исключение. В случае, когда *Visitor* заходит в
    `ifStatement`, он проверяет, что в обеих ветвях условного выражения было достигнуто
    `returnStatement`. Только в этом случае *Visitor* возвращает `true`, иначе - возвращается `false`,
    так как исполнение могло пойти по пути без `returnStatement'а` и не завершиться. Аналогично предыдущему
    виду статического анализа, в случае, когда `ifStatement` имеет условие, равное `true` или `false`,
    учитывается только результат полученный при обходе ветви `then` или `else`, соответственно.
* **Conflicting declarations checker**  
Проверка того, что в функции ни одна переменная не объявляется повторно. Проверка также запрещает
объявление в теле функции переменной, имя которой совпадает с именем какого-либо параметра данной функции.
    * *Checker* - [ConflictingDeclarationsChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/ConflictingDeclarationsChecker.kt)
    * *Visitor* - [ConflictingDeclarationsVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/ConflictingDeclarationsVisitor.kt)
    * *Exception* - [ConflictingDeclarationsException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/ConflictingDeclarationsException.kt)  
    
    **Идея алгоритма**  
    *Visitor* обходит дерево разбора, на вход методы *Visitor'а* получают `HashSet<String>`,
    содержащий названия объявленных переменных, изначально это множество пустое. Заходя в
    `variableDeclaration` *Visitor* проверяет, не была ли эта переменная уже объявлена ранее и нет
    ли у данной функции параметра с таким же названием. Если нет, то эта переменная добавляется
    во множество объявленных переменных. Иначе, выбрасывается исключение.
    
* **Wrong arguments checker**  
Проверка того, что ни одна функция не вызывается с неправильным количеством аргументов.
То есть если в объявлении функции указано *n* параметров, то вызвать ее можно только
с *n* параметрами, иначе данная проверка выбросит исключение.
    * *Checker* - [WrongArgumentsChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/WrongArgumentsChecker.kt)
    * *Visitor* - [WrongArgumentsVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/WrongArgumentsVisitor.kt)
    * *Exception* - [WrongArgumentsException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/WrongArgumentsException.kt)  
    
    **Идея алгоритма**  
    *Visitor* обходит дерево разбора, на вход методы *Visitor'а* получают `HashMap<String, Int>`, где
    ключ - название функции, значение - количество ее параметров. Изначально этот `HashMap` пустой.
    В начале разбора файла, *Visitor* добавляет в `HashMap` информацию о количестве параметров всех функций
    в данном файле. Затем *Visitor* обходит дерево разбора и, приходя в вызов функции,
    проверяет, вызвана ли она с правильным количеством аргументов. Если нет, выбрасывается исключение.
    
* **Empty body checker**  
Проверка того, что в программе нет пустых блоков кода. Данная проверка
выбросит исключение, если встретит функцию, блок `if` или `else`
с пустым телом.
    * *Checker* - [EmptyBodyChecker](src/main/kotlin/org/jetbrains/dummy/lang/checkers/EmptyBodyChecker.kt)
    * *Visitor* - [EmptyBodyVisitor](src/main/kotlin/org/jetbrains/dummy/lang/tree/visitors/EmptyBodyVisitor.kt)
    * *Exception* - [EmptyBodyException](src/main/kotlin/org/jetbrains/dummy/lang/exceptions/EmptyBodyException.kt)  
    
    **Идея алгоритма**  
    *Visitor* обходит дерево разбора и, встретив пустой блок кода (пустое тело функции или пустая ветвь условного выражения),
    выбрасывает исключение.
    
**Тесты**

Класс [DummyLanguageTestGenerated](src/test/kotlin/org/jetbrains/dummy/lang/DummyLanguageTestGenerated.kt)
содержит тесты приведенных видов статического анализа. Тесты были сгенерированы из
файлов в папке [testData](testData).

Тесты включают в себя:
* 5 программ на языке *Dummy language*, не содержащих ошибок
* 10 программ на языке *Dummy language*, содержащих ошибки, которые
отлавливаются *checker'ами*