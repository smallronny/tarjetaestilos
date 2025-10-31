class ItemContent{
    icon:string|null;
    description:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    page_content_id:number|null

    constructor(
        icon:string|null,
        description:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, id:number|null=null,
        page_content_id:number|null=null
    ){
        this.icon = icon;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.page_content_id = page_content_id;
    }
};
export default ItemContent;