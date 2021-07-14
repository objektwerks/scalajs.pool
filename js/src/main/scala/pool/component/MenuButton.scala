package pool.component

import com.raquo.laminar.api.L._

object MenuButton {
  val css = "w3-bar-item w3-button w3-margin w3-text-indigo"

  def apply(name: String): Button = button(cls(css), name)

  def apply(id: String, name: String): Button = button(idAttr(id), cls(css), name)
}