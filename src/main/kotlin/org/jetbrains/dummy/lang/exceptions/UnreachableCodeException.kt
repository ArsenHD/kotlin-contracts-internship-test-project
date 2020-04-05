package org.jetbrains.dummy.lang.exceptions

import org.jetbrains.dummy.lang.tree.Element

class UnreachableCodeException(val element: Element): Exception()