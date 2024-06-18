package com.github.nuko

abstract class LanguageException(val location: Option[Location], val message: String) extends Exception(message)
case class TyperException(override val location: Option[Location], override val message: String) extends LanguageException(location, message)
case class TyperPanic(override val location: Option[Location], override val message: String) extends LanguageException(location, message)
case class InterpreterException(override val location: Option[Location], override val message: String) extends LanguageException(location, message)
case class InterpreterPanic(override val location: Option[Location], override val message: String) extends LanguageException(location, "[PANIC]:" + message)
case class RewriterPanic(override val location: Option[Location], override val message: String) extends LanguageException(location, "[PANIC]:" + message)
