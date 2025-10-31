class Blog{    
    title:string|null;
    description:string|null;
    summary:string|null;
    publication_date:string|null;
    reading_timed:string|null;
    card_image:string|null;
    main_image:string|null;
    created_at:Date|null;
    updated_at:Date|null;
    deleted_at:Date|null;
    startDate:string|null;
    endDate:string|null;
    category_id:number[]|null;
    id:number|null;
    menu_id:number|null;

    constructor(title:string|null, description:string|null, summary:string|null, publication_date: string|null, reading_timed:string|null,  
        card_image:string|null,
        main_image:string|null,
        created_at:Date|null, updated_at:Date|null, deleted_at:Date|null, startDate:string|null=null, endDate:string|null=null,
        category_id:number[]|null=null, id:number|null=null,menu_id:number|null=null
    ){
        this.title = title;
        this.description = description;
        this.summary = summary;
        this.publication_date = publication_date;
        this.reading_timed = reading_timed;
        this.card_image = card_image;
        this.main_image = main_image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category_id = category_id;
        this.id = id;
        this.menu_id = menu_id;
    }
};
export default Blog;