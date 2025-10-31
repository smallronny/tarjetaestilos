class FormBlog{
    dni:string|null;
    phone:string|null;
    email:string|null;
    accept:boolean|null;
    created_at:Date|null;
    id:number|null;
    menu_id:number|null;

    constructor(
        dni:string|null,
        phone:string|null,
        email:string|null,
        accept:boolean|null,
        created_at:Date|null,
        menu_id:number|null=null,
        id:number|null
    ){
        this.dni = dni;
        this.phone = phone;
        this.email = email;
        this.accept = accept;
        this.created_at = created_at;
        this.id = id;
        this.menu_id = menu_id;
    }
};
export default FormBlog;