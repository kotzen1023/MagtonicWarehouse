package com.magtonic.magtonicwarehouse.model.sys


class User {
    companion object {
        const val USER_ACCOUNT = "userAccount"
        const val USER_NAME = "userName"
        const val PASSWORD = "password"
    }

    var userName: String = ""
    var userAccount: String = ""
    var password: String = ""
    //public String token;
    var isLogin = false


    /* object {

        fun getUser(context: Context): User {
            return EncryptShareHelper.getUser(context)
        }//getUser

        fun saveUser(context: Context, user: User) {

            EncryptShareHelper.saveUser(context, user)

        }//saveUser

        fun reset(user: User) {
            // user.token = "";
            user.password = ""
            //user.token = "";
            user.userAccount = ""
            user.userName = ""
            user.isLogin = false
        }

        fun removeUser(context: Context) {
            EncryptShareHelper.removeUser(context)
        }
    }*/

}//User
//User