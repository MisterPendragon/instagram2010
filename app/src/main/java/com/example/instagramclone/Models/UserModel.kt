package com.example.instagramclone.Models

class UserModel {
    private var name: String = ""
    private var nameAndSurname: String = ""
    private var phoneNumber: String = ""
    private var mail: String = ""
    private var uid: String = ""
    private  var url: String = ""

    constructor()
    constructor(name: String, nameAndSurname: String, phoneNumber: String, mail: String, uid: String, url: String) {
        this.name = name
        this.nameAndSurname = nameAndSurname
        this.phoneNumber = phoneNumber
        this.mail = mail
        this.uid = uid
        this.url = url
    }


    fun getName() : String {
        return name
    }
    fun getNameAndSurname() : String {
        return nameAndSurname
    }
    fun getPhoneNumber() : String {
        return phoneNumber
    }
    fun getMail() : String {
        return mail
    }
    fun getUid() : String {
        return uid
    }
    fun getUrl() : String {
        return url
    }

    fun setName (name:String){
        this.name = name
    }
    fun setNameAndSurname (nameAndSurname:String){
        this.nameAndSurname = nameAndSurname
    }
    fun setPhoneNumber (phoneNumber:String){
        this.phoneNumber = phoneNumber
    }
    fun setMail(mail:String){
        this.mail = mail
    }
    fun setUid(uid:String){
        this.uid = uid
    }
    fun setUrl(url:String){
        this.url = url
    }

}

