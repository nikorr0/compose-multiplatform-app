package org.company.app.repository


import org.company.app.model.UserDto
import java.nio.file.attribute.UserPrincipal

object Storage {
    var token: String? = null
    var currentUser: UserDto? = null
}