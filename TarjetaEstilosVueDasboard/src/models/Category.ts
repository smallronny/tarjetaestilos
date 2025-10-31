class Category{
    name:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;

    constructor(
        name:string|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null
    ){
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
    }
};
export default Category;