package com.renato.springbootstrap.exception

import java.util.function.Supplier

class NotFoundException(override var message : String) : RuntimeException(), Supplier<Throwable> {
    override fun get(): Throwable {
        return this
    }
}
