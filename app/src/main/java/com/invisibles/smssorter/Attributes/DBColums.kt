package com.invisibles.smssorter.Attributes

class DBColums {

    class SMS{
        companion object {
            const val NAME = "contactName"
            const val ID = "id"
            const val NUMBER = "contactNumber"
            const val TEXT = "text"
            const val TIME = "time"
            const val TYPE = "type"
            const val TO_FOLDER = "toFolder"
        }

    }

    class FOLDER{
        companion object{
            const val ID = "id"
            const val NAME = "folderName"
            const val ICON = "icon"
        }
    }
}