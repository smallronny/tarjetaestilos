class Menu{
    title:string|null;
    slug:string|null;
    icon:string|null;
    page:boolean|null;
    product:boolean|null;
    insurance:boolean|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;
    sections:number[]|null;
    types:number[]|null;

    constructor(
        title:string|null,
        slug:string|null,
        icon:string|null,
        page:boolean|null,
        product:boolean|null,
        insurance:boolean|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null,sections:number[]|null=null,types:number[]|null=null
    ){
        this.title = title;
        this.slug = slug;
        this.icon = icon;
        this.page = page;
        this.product = product;
        this.insurance = insurance;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
        this.sections = sections;
        this.types = types;
    }
};
export default Menu;