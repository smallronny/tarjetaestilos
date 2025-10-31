class Button{
    title:string|null;
    add_class:string|null;
    link:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    banners_id:number|null

    constructor(
        title:string|null,
        add_class:string|null,
        link:string|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null, id:number|null=null,
        banners_id:number|null=null
    ){
        this.title = title;
        this.add_class = add_class;
        this.link = link;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.banners_id = banners_id;
    }
};
export default Button;