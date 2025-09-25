using System.ComponentModel.DataAnnotations;

namespace CultureXAPI.DTOs
{
    public class UpdateUserProfileDTO
    {

        [Required]
        public string DisplayName { get; set; }

        public string? ProfilePictureUrl { get; set; }

        public string PreferredLanguage { get; set; } = "en";

    }
}
