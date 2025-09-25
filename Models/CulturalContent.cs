namespace CultureXAPI.Models
{
    public class CulturalContent
    {

        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid CountryId { get; set; }
        public Guid CategoryId { get; set; }
        public string Title { get; set; }
        public string? Content { get; set; }
        public string? Dos { get; set; } // JSON array
        public string? Donts { get; set; } // JSON array
        public string? Examples { get; set; } // JSON array
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public Country Country { get; set; }
        public CulturalCategory Category { get; set; }

    }
}
