class Promotion{
    title:string|null;
    description:string|null;
    image:string|null;
    discount:string|null;
    isnew:boolean|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    department_id:number|null;
    categoryIds:number[]|null;
    id:number|null;

    featured:boolean|null;
    promotion_link:string|null;
    promotion_map:string|null;
    outstanding:boolean|null;
    terms_conditions:string|null;

    affiliates_id:number|null;
    menu_id:number|null;
    home:boolean|null;
    exclusive:boolean|null;

    constructor(
        title:string|null,
        description:string|null,
        image:string|null,
        discount:string|null,
        isnew:boolean|null,
        created_at:Date|null, 
        updated_at:Date|null, 
        deleted_at:Date|null,
        department_id:number|null,
        categoryIds:number[]|null=null, 
        id:number|null=null,
        outstanding:boolean|null=null,
        featured:boolean|null=null,
        promotion_link:string|null=null,
        promotion_map:string|null=null,
        terms_conditions:string|null=null,
        affiliates_id:number|null=null,
        menu_id:number|null=null,
        home:boolean|null=null,
        exclusive:boolean|null=null,
    ){
        this.title = title;
        this.description = description;
        this.image = image;
        this.discount = discount;
        this.isnew = isnew;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.department_id = department_id;
        this.categoryIds = categoryIds;
        this.id = id;
        this.outstanding = outstanding;
        this.featured = featured;
        this.promotion_link = promotion_link;
        this.promotion_map = promotion_map;
        this.terms_conditions = terms_conditions;
        this.affiliates_id = affiliates_id;
        this.menu_id = menu_id;
        this.home = home;
        this.exclusive = exclusive;
    }
};
export default Promotion;