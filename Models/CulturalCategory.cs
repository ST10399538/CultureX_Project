namespace CultureXAPI.Models
{
    public class CulturalCategory
    {

        public Guid Id { get; set; } = Guid.NewGuid();
        public string Name { get; set; }
        public string? Description { get; set; }
        public string? IconUrl { get; set; }
        public int SortOrder { get; set; } = 0;
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public ICollection<CulturalContent> Contents { get; set; } = new List<CulturalContent>();

    }
}
