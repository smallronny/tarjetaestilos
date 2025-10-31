class Department{
    name:String|null;
    phone:String|null;
    label_phone:String|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;

    constructor(
        name:String|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null, phone:string|null=null, label_phone:string|null=null
    ){
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.phone = phone;
        this.label_phone = label_phone;
    }
};
export default Department;