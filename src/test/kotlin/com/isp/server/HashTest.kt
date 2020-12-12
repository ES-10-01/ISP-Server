package com.isp.server

import com.isp.server.models.UserModel
import com.isp.server.util.hash
import com.isp.server.util.hashUserPassword
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class HashTest {

        @Test
        fun hashTest() {
                val sourcePasswordValues: Array<String> = arrayOf("asd", "jjdfj157uA", "0991_99saFFa")
                val expectedHashValues: Array<String>  = arrayOf(
                        "688787d8ff144c502c7f5cffaafe2cc588d86079f9de88304c26b0cb99ce91c6",
                        "43f556d4a068484243249d8d40e40ba45de2e5c6a2dee9f0be883b6ad36394b4",
                        "41f4ab27d6a1c4e06280a7bcb50409fa18840a35657611eefbb12b732ef49f0a")

                for (index in sourcePasswordValues.indices){
                        Assertions.assertEquals(hash(sourcePasswordValues[index]), expectedHashValues[index])
                }
        }

        @Test
        fun hashUserPasswordTest() {
                val firstModel: UserModel = UserModel(1, "asd", "asd", "asd", "admin", mutableListOf())
                val secondModel: UserModel = hashUserPassword(firstModel)
                Assertions.assertEquals(hash(firstModel.password), secondModel.password)
        }

}