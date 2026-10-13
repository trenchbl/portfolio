package com.blog.post.categories;

/**
 * @param postCount all posts in the category, drafts included
 */
public record AdminCategoryItem(Long id, String name, String slug, long postCount) {

    public boolean inUse() {
        return postCount > 0;
    }
}