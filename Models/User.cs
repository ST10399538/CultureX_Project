using System.ComponentModel.DataAnnotations;

namespace CultureXAPI.Models
{
    public class User
    {

        public Guid Id { get; set; } = Guid.NewGuid();

        [Required]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        public string DisplayName { get; set; }

        public string? ProfilePictureUrl { get; set; }

        public string PreferredLanguage { get; set; } = "en";

        public bool BiometricEnabled { get; set; } = false;

        public string? NotificationPreferences { get; set; } // JSON

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public ICollection<UserItinerary> Itineraries { get; set; } = new List<UserItinerary>();
        public ICollection<UserFavorite> Favorites { get; set; } = new List<UserFavorite>();
        public ICollection<UserSession> Sessions { get; set; } = new List<UserSession>();

    }
}
