import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import "react-circular-progressbar/dist/styles.css";

const CircularProgressBarLoader = ({ className = "", value = "" }) => {
  return (
    <div className={`circluar-progress-bar ${className}`}>
      <CircularProgressbar
        value={value}
        text={`${value}%`}
        styles={buildStyles({
          pathTransitionDuration: 0.5,
          pathColor: `#b99750`,
          textColor: "black",
          textSize: "1rem",
        })}
      />
    </div>
  );
};

export default CircularProgressBarLoader;
