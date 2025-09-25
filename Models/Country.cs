namespace CultureXAPI.Models
{
    public class Country
    {

        public Guid Id { get; set; } = Guid.NewGuid();
        public string Name { get; set; }
        public string CountryCode { get; set; }
        public string? FlagImageUrl { get; set; }
        public string? Description { get; set; }
        public string? Timezone { get; set; }
        public string? Currency { get; set; }
        public string? EmergencyContacts { get; set; } // JSON
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public ICollection<CulturalContent> CulturalContents { get; set; } = new List<CulturalContent>();
        public ICollection<UserItinerary> Itineraries { get; set; } = new List<UserItinerary>();

    }
}
