class User{
    fullname:String|null;
    image:String|null;
    email:String|null;
    phone:String|null;
    password:String|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;

    constructor(fullname:String|null, image:String|null, email:String|null, phone: String|null, password:String|null,  created_at:Date|null, updated_at:Date|null, deleted_at:Date|null){
        this.fullname = fullname;
        this.image = image;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }
}
export default User;