package com.github.kmizu.scomb

enum Result[+T] {
  def value: Option[T] = this match {
    case Success(v) => Some(v)
    case Failure(location, message) => None
  }
  case Success(semanticValue: T)
  case Failure(location: Location, message: String) extends Result[Nothing]
}