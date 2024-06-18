package com.github.nuko

import java.util.ArrayList
import java.util.HashMap
import org.scalatest.diagrams.Diagrams
import org.scalatest.funspec.AnyFunSpec

trait SpecHelper extends AnyFunSpec with Diagrams {
  val E = new Evaluator
  def listOf[T](elements: T*): ArrayList[T] = {
    val newList = new ArrayList[T]
    elements.foreach{e =>
      newList.add(e)
    }
    newList
  }
  def setOf[T](elements: T*): java.util.Set[T] = {
    val newSet = new java.util.HashSet[T]
    elements.foreach{e =>
      newSet.add(e)
    }
    newSet
  }
  def mapOf[K, V](kvs: (K, V)*): HashMap[K, V] = {
    val newMap = new HashMap[K, V]
    for((k, v) <- kvs) {
      newMap.put(k, v)
    }
    newMap
  }
  def expect[A, B](label: String)(expectation: (String, Value)): Unit = expectation match {
    case (actual, expected) =>
      it(label) {
        assertResult(expected)(E.evaluateString(actual))
      }
  }
}
