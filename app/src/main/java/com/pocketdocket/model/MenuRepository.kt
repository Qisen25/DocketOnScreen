package com.pocketdocket.model

/**
 * Singleton class for storing and caching existing menu items at run time
 */
class MenuRepository {
    companion object {
        var menus: MutableList<Catalogue> = mutableListOf<Catalogue>()
    }
}