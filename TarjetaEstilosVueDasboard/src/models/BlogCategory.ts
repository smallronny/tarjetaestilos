class BlogCategory{
    blog_id:number|null;
    category_id:number|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    id:number|null;

    constructor(
        blog_id:number|null,
        category_id:number|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, id:number|null=null
    ){
        this.blog_id = blog_id;
        this.category_id = category_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.id = id;
    }
};
export default BlogCategory;