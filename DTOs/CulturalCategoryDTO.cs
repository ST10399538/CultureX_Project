namespace CultureXAPI.DTOs
{
    public class CulturalCategoryDTO
    {

        public Guid Id { get; set; }
        public string Name { get; set; }
        public string? Description { get; set; }
        public string? IconUrl { get; set; }
        public int SortOrder { get; set; }

    }
}
