class PromotionsCategories{
    promotion_id:number|null;
    promotion_category_id:number|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;

    constructor(
        promotion_id:number|null,
        promotion_category_id:number|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null
    ){
        this.promotion_id = promotion_id;
        this.promotion_category_id = promotion_category_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
    }
};
export default PromotionsCategories;