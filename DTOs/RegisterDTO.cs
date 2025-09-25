using System.ComponentModel.DataAnnotations;

namespace CultureXAPI.DTOs
{
    public class RegisterDTO
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        [MinLength(6)]
        public string Password { get; set; }

        [Required]
        public string DisplayName { get; set; }

        public string PreferredLanguage { get; set; } = "en";
    }
}
