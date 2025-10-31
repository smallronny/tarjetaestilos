class PromotionCategory{
    name:string|null;
    icono:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;

    constructor(
        name:string|null,        
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null,icono:string|null=null
    ){
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.icono = icono;
    }
};
export default PromotionCategory;